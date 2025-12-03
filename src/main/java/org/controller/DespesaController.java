package org.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.dao.DespesaDAO;
import org.model.Despesa;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class DespesaController implements Initializable {

    // --- Componentes do FXML ---
    @FXML private TextField txtDescricao;
    @FXML private TextField txtValor;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private DatePicker dpData;
    @FXML private Label lblTotal;

    @FXML private TableView<Despesa> tabelaDespesas;
    @FXML private TableColumn<Despesa, Integer> colId;
    @FXML private TableColumn<Despesa, String> colDescricao;
    @FXML private TableColumn<Despesa, String> colCategoria;
    @FXML private TableColumn<Despesa, LocalDate> colData;
    @FXML private TableColumn<Despesa, Double> colValor;

    @FXML private Button btnAdicionar;
    @FXML private Button btnAtualizar;
    @FXML private Button btnExcluir;

    // --- Dependências ---
    private final DespesaDAO dao = new DespesaDAO();
    private ObservableList<Despesa> listaDespesas = FXCollections.observableArrayList();
    private Despesa despesaSelecionada; // Para saber qual estamos editando

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabela();
        carregarDados();
        configurarBotoes();
        configurarSelecaoTabela();
    }

    private void configurarTabela() {
        // Vincula as colunas aos atributos da classe Despesa (precisa dos Getters do Lombok)
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        // Formatação visual da Data (dd/MM/yyyy)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colData.setCellFactory(column -> new TableCell<Despesa, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        // Formatação visual do Valor (R$)
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        colValor.setCellFactory(column -> new TableCell<Despesa, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(item));
                }
            }
        });

        tabelaDespesas.setItems(listaDespesas);
    }

    private void carregarDados() {
        listaDespesas.clear();
        listaDespesas.addAll(dao.listarDespesas());
        atualizarTotal();
    }

    private void configurarBotoes() {
        // Ação Adicionar
        btnAdicionar.setOnAction(event -> {
            try {
                if (validarCampos()) {
                    Despesa nova = new Despesa();
                    nova.setDescricao(txtDescricao.getText());
                    nova.setValor(Double.parseDouble(txtValor.getText().replace(",", "."))); // Troca vírgula por ponto
                    nova.setCategoria(cbCategoria.getValue());
                    nova.setData(dpData.getValue());

                    dao.salvarDespesa(nova);
                    carregarDados();
                    limparCampos();
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Erro", "O valor deve ser numérico (ex: 150.50)");
            }
        });

        // Ação Atualizar
        btnAtualizar.setOnAction(event -> {
            if (despesaSelecionada == null) {
                mostrarAlerta("Aviso", "Selecione uma despesa na tabela para atualizar.");
                return;
            }
            if (validarCampos()) {
                despesaSelecionada.setDescricao(txtDescricao.getText());
                despesaSelecionada.setValor(Double.parseDouble(txtValor.getText().replace(",", ".")));
                despesaSelecionada.setCategoria(cbCategoria.getValue());
                despesaSelecionada.setData(dpData.getValue());

                dao.atualizarDespesa(despesaSelecionada);
                carregarDados();
                limparCampos();
                despesaSelecionada = null; // Reseta seleção
            }
        });

        // Ação Excluir
        btnExcluir.setOnAction(event -> {
            Despesa selecionada = tabelaDespesas.getSelectionModel().getSelectedItem();
            if (selecionada != null) {
                dao.excluirDespesa(selecionada.getId());
                carregarDados();
                limparCampos();
            } else {
                mostrarAlerta("Aviso", "Selecione uma despesa para excluir.");
            }
        });
    }

    private void configurarSelecaoTabela() {
        // Quando clicar na tabela, preenche os campos lá em cima
        tabelaDespesas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                despesaSelecionada = newSelection;
                txtDescricao.setText(newSelection.getDescricao());
                txtValor.setText(String.valueOf(newSelection.getValor()));
                cbCategoria.setValue(newSelection.getCategoria());
                dpData.setValue(newSelection.getData());
            }
        });
    }

    private void atualizarTotal() {
        double total = dao.calcularTotalDespesas();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        lblTotal.setText(currencyFormat.format(total));
    }

    private void limparCampos() {
        txtDescricao.clear();
        txtValor.clear();
        cbCategoria.getSelectionModel().clearSelection();
        dpData.setValue(null);
        tabelaDespesas.getSelectionModel().clearSelection();
        despesaSelecionada = null;
    }

    private boolean validarCampos() {
        if (txtDescricao.getText().isEmpty() || txtValor.getText().isEmpty() ||
                cbCategoria.getValue() == null || dpData.getValue() == null) {
            mostrarAlerta("Erro", "Preencha todos os campos!");
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}