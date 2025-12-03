package org.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.dao.DespesaDAO;
import org.model.Despesa;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DespesaController {

    // Vínculo com os componentes do FXML
    @FXML private TextField txtDescricao;
    @FXML private TextField txtValor;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private DatePicker dpData;
    @FXML private Label lblTotal;

    // Tabela e Colunas
    @FXML private TableView<Despesa> tabelaDespesas;
    @FXML private TableColumn<Despesa, Integer> colId;
    @FXML private TableColumn<Despesa, String> colDescricao;
    @FXML private TableColumn<Despesa, String> colCategoria;
    @FXML private TableColumn<Despesa, LocalDate> colData;
    @FXML private TableColumn<Despesa, Double> colValor;

    // Botões (para configurar ações via código se necessário)
    @FXML private Button btnAdicionar;
    @FXML private Button btnAtualizar;
    @FXML private Button btnExcluir;

    private final DespesaDAO dao = new DespesaDAO();
    private final ObservableList<Despesa> listaDespesas = FXCollections.observableArrayList();

    // Método executado automaticamente quando a tela abre
    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();
        configurarBotoes();
        configurarSelecaoTabela();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        // Formatação da Data na Tabela (dd/MM/yyyy)
        DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colData.setCellFactory(column -> new TableCell<Despesa, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(myDateFormatter.format(item));
                }
            }
        });

        // Formatação de Moeda na Tabela (R$)
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
    }

    private void carregarDados() {
        listaDespesas.clear();
        listaDespesas.addAll(dao.listarDespesas());
        tabelaDespesas.setItems(listaDespesas);
        atualizarTotal();
    }

    private void atualizarTotal() {
        double total = dao.calcularTotalDespesas();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        lblTotal.setText(currencyFormat.format(total));
    }

    private void configurarBotoes() {
        btnAdicionar.setOnAction(e -> salvarDespesa());
        btnAtualizar.setOnAction(e -> atualizarDespesa());
        btnExcluir.setOnAction(e -> excluirDespesa());
    }

    // Preenche os campos quando clica na tabela
    private void configurarSelecaoTabela() {
        tabelaDespesas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtDescricao.setText(newSelection.getDescricao());
                // Remove o R$ para edição se necessário, ou mostra valor cru
                txtValor.setText(String.valueOf(newSelection.getValor()));
                cbCategoria.setValue(newSelection.getCategoria());
                dpData.setValue(newSelection.getData());
            }
        });
    }

    private void salvarDespesa() {
        try {
            Despesa d = montarObjetoDosCampos();
            d.setId(0);
            dao.salvarDespesa(d);

            limparCampos();
            carregarDados();
        } catch (Exception e) {
            mostrarAlerta("Erro ao Salvar", "Verifique os campos: " + e.getMessage());
        }
    }

    private void atualizarDespesa() {
        Despesa selecionada = tabelaDespesas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta("Aviso", "Selecione uma despesa na tabela para atualizar.");
            return;
        }

        try {
            Despesa d = montarObjetoDosCampos();
            d.setId(selecionada.getId()); // Mantém o ID original
            dao.atualizarDespesa(d);

            limparCampos();
            carregarDados();
        } catch (Exception e) {
            mostrarAlerta("Erro ao Atualizar", e.getMessage());
        }
    }

    private void excluirDespesa() {
        Despesa selecionada = tabelaDespesas.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAlerta("Aviso", "Selecione uma despesa para excluir.");
            return;
        }

        dao.excluirDespesa(selecionada.getId());
        limparCampos();
        carregarDados();
    }

    private Despesa montarObjetoDosCampos() {
        String descricao = txtDescricao.getText();
        String valorStr = txtValor.getText().replace(",", "."); // Aceita vírgula ou ponto
        String categoria = cbCategoria.getValue();
        LocalDate data = dpData.getValue();

        if (descricao.isEmpty() || valorStr.isEmpty() || categoria == null || data == null) {
            throw new IllegalArgumentException("Preencha todos os campos!");
        }

        double valor = Double.parseDouble(valorStr);

        // O ID será definido depois dependendo se é Salvar ou Atualizar
        return new Despesa(0, descricao, valor, data, categoria);
    }

    private void limparCampos() {
        txtDescricao.clear();
        txtValor.clear();
        cbCategoria.getSelectionModel().clearSelection();
        dpData.setValue(LocalDate.now());
        tabelaDespesas.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}