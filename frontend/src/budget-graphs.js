import ApexCharts from 'apexcharts';

window.renderBudgetChart = function (host, chartType, config) {
  if (!host) return;

  if (host._apexChart) {
    try {
      host._apexChart.destroy();
    } catch (e) {
      console.warn("Could not destroy previous chart", e);
    }
    host._apexChart = null;
  }

  host.innerHTML = "";

  const options = {
    chart: {
      type: chartType,
      height: config.height || 220,
      width: "100%",
      toolbar: { show: false },
      animations: { enabled: true },
      background: "transparent"
    },
    series: config.series || [],
    labels: config.labels || [],
    colors: config.colors || undefined,
    legend: config.legend || { position: "bottom" },
    stroke: config.stroke || { lineCap: "round", width: 2 },
    dataLabels: config.dataLabels !== undefined ? config.dataLabels : { enabled: true },
    plotOptions: config.plotOptions || {},
    xaxis: config.xaxis || {},
    yaxis: config.yaxis || {},
    grid: config.grid || {
      borderColor: "#e2e8f0",
      strokeDashArray: 3
    },
    tooltip: config.tooltip || {
      theme: "light"
    }
  };

  if (chartType === "radialBar") {
    options.plotOptions = options.plotOptions || {};
    options.plotOptions.radialBar = options.plotOptions.radialBar || {};
    options.plotOptions.radialBar.dataLabels = options.plotOptions.radialBar.dataLabels || {};
    options.plotOptions.radialBar.dataLabels.value =
      options.plotOptions.radialBar.dataLabels.value || {};

    options.plotOptions.radialBar.dataLabels.value.formatter = function (val) {
      return Number(val).toFixed(1) + "%";
    };
  }

  const chart = new ApexCharts(host, options);
  host._apexChart = chart;
  chart.render();
  console.log("renderBudgetChart called", chartType, config);
};
